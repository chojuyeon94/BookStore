package solo.bookstore.global.response;

import io.micrometer.core.lang.Nullable;
import lombok.Getter;

@Getter
public class SingleResponseDto<T> {

    private T data;
    public SingleResponseDto(@Nullable T data){
        this.data = data;
    }
}
