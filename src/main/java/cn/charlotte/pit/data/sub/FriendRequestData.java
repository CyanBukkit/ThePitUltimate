package cn.charlotte.pit.data.sub;

import cn.charlotte.pit.util.time.Duration;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 2022/7/29<br>
 * ThePit-B<br>
 *
 * @author huanmeng_qwq
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class FriendRequestData {
    private String sender;
    private String target;
    private long time = 0;

    @JsonIgnore
    public boolean isExpired() {
        return (time + 1000 * 60 * 5) < System.currentTimeMillis();
    }
}
