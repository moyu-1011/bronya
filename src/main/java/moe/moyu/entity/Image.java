package moe.moyu.entity;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 图片实体对象
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@EqualsAndHashCode
public class Image {
    private Long id;
    private Long ownerId;
    private String keyword;
    private String imageId;
    private String imageUrl;
    private boolean deleted;
    private LocalDateTime addTime;
    private LocalDateTime deleteTime;
}
