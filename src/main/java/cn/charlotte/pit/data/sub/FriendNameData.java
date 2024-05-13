package cn.charlotte.pit.data.sub;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode
public class FriendNameData {
    private String uuid;
    private String name;
}
