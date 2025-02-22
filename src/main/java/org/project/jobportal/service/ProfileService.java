package org.project.jobportal.service;

import org.project.jobportal.dto.ProfileDTO;
import org.project.jobportal.exception.JobPortalException;
import org.project.jobportal.model.Profile;
import org.project.jobportal.repository.ProfileRepository;
import org.project.jobportal.utils.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProfileService {
    @Autowired
    private ProfileRepository profileRepository;

    public Long createProfile(String email) throws JobPortalException {
        Profile profile = new Profile();
        profile.setId(Utilities.getNextSequence("profiles"));
        profile.setEmail(email);
        profile.setSkills(new ArrayList<>());
        profile.setExperiences(new ArrayList<>());
        profile.setCertifications(new ArrayList<>());
        profileRepository.save(profile);
        return profile.getId();
    }

    public ProfileDTO getProfile(Long id) throws JobPortalException {
        return profileRepository.findById(id).orElseThrow(()->new JobPortalException("PROFILE_NOT_FOUND")).toDTO();
    }

    public ProfileDTO updateProfile(ProfileDTO profileDTO) throws JobPortalException {
        profileRepository.findById(profileDTO.getId()).orElseThrow(()->new JobPortalException("PROFILE_NOT_FOUND")).toDTO();
        profileRepository.save(profileDTO.toEntity());
        return profileDTO;
    }

    public List<ProfileDTO> getAllProfile() {
        return profileRepository.findAll().stream().map((x)->x.toDTO()).toList();
    }
}
