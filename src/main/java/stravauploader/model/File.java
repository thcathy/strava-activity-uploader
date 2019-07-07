package stravauploader.model;

import java.util.Arrays;
import java.util.StringJoiner;

public class File {
    public String name;
    public String type;
    public byte[] content;

    public File(String name, byte[] content, String type) {
        this.name = name;
        this.content = content;
        this.type = type;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", File.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("type='" + type + "'")
                .add("content=" + Arrays.toString(content))
                .toString();
    }
}
