package pl.dir.sync.common.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class FileKeyDto {
    private String relativePath;
    private boolean isDir;
}