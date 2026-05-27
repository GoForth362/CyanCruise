package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * CareerLoop resource feed response for webapp consumption.
 */
public class CareerResourceFeedDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String status;
    private String message;
    private LocalDateTime updatedAt;
    private List<CareerResourceCardDto> articles = new ArrayList<CareerResourceCardDto>();
    private List<CareerResourceCardDto> videos = new ArrayList<CareerResourceCardDto>();
    private List<CareerResourceCardDto> consultations = new ArrayList<CareerResourceCardDto>();
    private List<CareerResourceCardDto> careerPaths = new ArrayList<CareerResourceCardDto>();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<CareerResourceCardDto> getArticles() {
        return articles;
    }

    public void setArticles(List<CareerResourceCardDto> articles) {
        this.articles = articles;
    }

    public List<CareerResourceCardDto> getVideos() {
        return videos;
    }

    public void setVideos(List<CareerResourceCardDto> videos) {
        this.videos = videos;
    }

    public List<CareerResourceCardDto> getConsultations() {
        return consultations;
    }

    public void setConsultations(List<CareerResourceCardDto> consultations) {
        this.consultations = consultations;
    }

    public List<CareerResourceCardDto> getCareerPaths() {
        return careerPaths;
    }

    public void setCareerPaths(List<CareerResourceCardDto> careerPaths) {
        this.careerPaths = careerPaths;
    }
}
