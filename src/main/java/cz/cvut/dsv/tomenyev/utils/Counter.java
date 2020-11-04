package cz.cvut.dsv.tomenyev.utils;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Counter implements Serializable {

    private static Counter counter;

    @NonNull
    private Integer i;

    public Integer getI() {
        return i;
    }

    public Integer inc() {
        return ++i;
    }

    public static Counter getInstance() {
        if(Objects.isNull(counter))
            counter = new Counter(0);
        return counter;
    }

    public static Counter setInstance(Integer i) {
        counter = new Counter(i);
        return counter;
    }
}
