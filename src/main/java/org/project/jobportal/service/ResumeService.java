package org.project.jobportal.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.project.jobportal.dto.ResumeDTO;
import org.project.jobportal.exception.JobPortalException;
import org.project.jobportal.model.Resume;
import org.project.jobportal.repository.ResumeRepository;
import org.project.jobportal.utils.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

@Service
public class ResumeService {

    @Autowired
    private ResumeRepository resumeRepository;

    public String extractText(MultipartFile file) throws IOException {
        String fileType = file.getContentType();

        if (fileType.equals("application/pdf")) {
            try (PDDocument document = PDDocument.load(file.getInputStream())) {
                PDFTextStripper pdfStripper = new PDFTextStripper();
                return pdfStripper.getText(document);
            }
        } else if (fileType.equals("application/msword") || fileType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            try (InputStream is = file.getInputStream();
                 XWPFDocument document = new XWPFDocument(is)) {
                StringBuilder text = new StringBuilder();
                List<XWPFParagraph> paragraphs = document.getParagraphs();
                for (XWPFParagraph para : paragraphs) {
                    text.append(para.getText()).append("\n");
                }
                return text.toString();
            }
        } else {
            throw new IOException("Unsupported file format. Please upload a PDF or DOCX file.");
        }
    }

    public ResumeDTO saveResume(ResumeDTO resumeDTO) throws JobPortalException {
        resumeDTO.setResumeId(Utilities.getNextSequence("resumeanalyzer"));
        Resume resume = resumeDTO.toEntity();
        Resume saveResume =  resumeRepository.save(resume);
        return saveResume.toDTO();
    }

    public ResumeDTO getLatestResumeByUserId(String userId) {
        return resumeRepository.findFirstByUserIdOrderByIdDesc(userId);
    }
}
