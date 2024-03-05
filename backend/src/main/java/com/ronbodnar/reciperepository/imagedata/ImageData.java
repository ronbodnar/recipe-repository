package com.ronbodnar.reciperepository.imagedata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "images")
public class ImageData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uri")
    private String uri;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "width")
    private int width;

    @Column(name = "height")
    private int height;

    /**
     * The raw binary data for the image created in front-end recipe creation.
     */
    @Transient
    @JsonIgnore
    private byte[] data;

}