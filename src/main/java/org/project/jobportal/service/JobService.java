package org.project.jobportal.service;

import org.project.jobportal.dto.*;
import org.project.jobportal.exception.JobPortalException;
import org.project.jobportal.model.Applicant;
import org.project.jobportal.model.Job;
import org.project.jobportal.repository.JobRepository;
import org.project.jobportal.utils.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class JobService {
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private NotificationService notificationService;

    public JobDTO postJob(JobDTO jobDTO) throws JobPortalException {
        if(jobDTO.getId() == null || jobDTO.getId()==0){
            jobDTO.setId(Utilities.getNextSequence("jobs"));
            jobDTO.setPostTime(LocalDateTime.now());
            NotificationDTO notiDTO = new NotificationDTO();
            notiDTO.setAction("Job Posted");
            notiDTO.setMessage("Job Posted Successfully for :"+jobDTO.getJobTitle() + "at" + jobDTO.getCompany());
            notiDTO.setUserId(jobDTO.getPostedBy());
            notiDTO.setRoute("/posted-jobs/" + jobDTO.getId());
            try {
                notificationService.sendNotification(notiDTO);
            } catch (JobPortalException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            Job job = jobRepository.findById(jobDTO.getId()).orElseThrow(()->new JobPortalException("JOB_NOT_FOUND"));
            if(job.getJobStatus().equals(JobStatus.DRAFT)||jobDTO.getJobStatus().equals(JobStatus.CLOSED))
                jobDTO.setPostTime(LocalDateTime.now());
        }
        return jobRepository.save(jobDTO.toEntity()).toDTO();
    }

    public List<JobDTO> getAllJobs() {
        return jobRepository.findAll().stream().map((x)->x.toDTO()).toList();
    }

    public JobDTO getJob(Long id) throws JobPortalException {
        return jobRepository.findById(id).orElseThrow(()->new JobPortalException("JOB_NOT_FOUND")).toDTO();
    }

    public void applyJob(long id, ApplicantDTO applicantDTO) throws JobPortalException{
        Job job=jobRepository.findById(id).orElseThrow(()->new JobPortalException("JOB_NOT_FOUND"));
        List<Applicant>applicants=job.getApplicants();
        if (applicants==null) applicants=new ArrayList<>();
        boolean alreadyApplied = applicants.stream()
                .anyMatch(applicant -> Objects.equals(applicant.getApplicantId(), applicantDTO.getApplicantId()));
        if (alreadyApplied) {
            throw new JobPortalException("JOB_APPLIED_ALREADY");
        }
//        if(applicants.stream().filter((x)->x.getApplicantId()==applicantDTO.getApplicantId()).toList().size()>0)throw new JobPortalException("JOB_APPLIED_ALREADY");
        applicantDTO.setApplicationStatus(ApplicationStatus.APPLIED);
        applicants.add(applicantDTO.toEntity());
        job.setApplicants(applicants);
        jobRepository.save(job);
    }

    public List<JobDTO> getJobPostedBy(Long id) {
        return jobRepository.findByPostedBy(id).stream().map((x)->x.toDTO()).toList();
    }

    public void changeAppStatus(Application application) throws JobPortalException{
        Job job=jobRepository.findById(application.getId()).orElseThrow(()->new JobPortalException("JOB_NOT_FOUND"));
        List<Applicant>applicants=job.getApplicants().stream().map((x)->{
            if(application.getApplicantId()==x.getApplicantId()){
                x.setApplicationStatus(application.getApplicationStatus());
                if(application.getApplicationStatus().equals(ApplicationStatus.INTERVIEWING)) {
                    x.setInterviewTime(application.getInterviewTime());
                    NotificationDTO notiDTO = new NotificationDTO();
                    notiDTO.setAction("Interview Scheduled");
                    notiDTO.setMessage("Interview Scheduled for job id:"+application.getId());
                    notiDTO.setUserId(application.getApplicantId());
                    notiDTO.setRoute("/job-history");
                    try {
                        notificationService.sendNotification(notiDTO);
                    } catch (JobPortalException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return x;
        }).toList();
        job.setApplicants(applicants);
        jobRepository.save(job);
    }
}
