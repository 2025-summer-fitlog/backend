package fitlog.backend.Dto;

public class RecommendationDto {

    private Long id;
    private String title;
    private String videoUrl;
    private String description;
    private String bodyPart;
    private String warning;
    private String equipment;
    private String effect;

    public RecommendationDto() {}

    public RecommendationDto(Long id, String title, String videoUrl, String description,
                             String bodyPart, String warning, String equipment, String effect) {
        this.id = id;
        this.title = title;
        this.videoUrl = videoUrl;
        this.description = description;
        this.bodyPart = bodyPart;
        this.warning = warning;
        this.equipment = equipment;
        this.effect = effect;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBodyPart() {
        return bodyPart;
    }

    public void setBodyPart(String bodyPart) {
        this.bodyPart = bodyPart;
    }

    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }
}

