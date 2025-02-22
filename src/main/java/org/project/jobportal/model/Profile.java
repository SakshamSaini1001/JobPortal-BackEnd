package org.project.jobportal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.project.jobportal.dto.Certifications;
import org.project.jobportal.dto.Experience;
import org.project.jobportal.dto.ProfileDTO;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Base64;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "profiles")
public class Profile {
    @Id
    private Long id;
    private String name;
    private String email;
    private String jobTitle;
    private String company;
    private String location;
    private String about;
    private byte[] picture;
    private Long totalExp;
    private List<String> skills;
    private List<Experience> experiences;
    private List<Certifications> certifications;
    private List<Long> savedJobs;
    
    public ProfileDTO toDTO(){
        return new ProfileDTO(this.id,this.name,this.email,this.jobTitle,this.company,this.location,this.about,this.picture!=null? Base64.getEncoder().encodeToString(this.picture):null,this.totalExp,this.skills,this.experiences,this.certifications,this.savedJobs);
    }
}
