package cn.charlotte.pit.data.sub;

import cn.charlotte.pit.ThePit;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mongodb.WriteConcern;
import io.netty.util.internal.ConcurrentSet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.mongojack.DBQuery;

import java.util.*;

/**
 * 2022/7/29<br>
 * ThePit-B<br>
 *
 * @author huanmeng_qwq
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FriendData {
    private String uuid;
    private Set<FriendNameData> friends = new HashSet<>();
    private Set<FriendRequestData> friendRequestDatas = new HashSet<>();

    @JsonIgnore
    public boolean isFriend(UUID uuid) {
        return friends.stream().anyMatch(e -> e.getUuid().equals(uuid.toString()));
    }

    @JsonIgnore
    public boolean isRequested(UUID target) {
        friendRequestDatas.removeIf(FriendRequestData::isExpired);
        return friendRequestDatas.stream().anyMatch(e -> e.getTarget().equals(target.toString()));
    }

    @JsonIgnore
    public boolean isRequesting(UUID sender) {
        friendRequestDatas.removeIf(FriendRequestData::isExpired);
        return friendRequestDatas.stream().anyMatch(e -> e.getSender().equals(sender.toString()));
    }

    @JsonIgnore
    public void accept(UUID target, String name) {
        friendRequestDatas.removeIf(e -> e.getSender().equals(target.toString()) || e.getTarget().equals(target.toString()));
        friends.add(new FriendNameData(target.toString(), name));
    }

    @JsonIgnore
    public void remove(UUID target) {
        friends.removeIf(e -> e.getUuid().equals(target.toString()));
    }

    @JsonIgnore
    public void save() {
        ThePit.getInstance()
                .getMongoDB()
                .getFriendCollection()
                .replaceOne(DBQuery.is("uuid", this.uuid), this, true, WriteConcern.NORMAL);
    }

    @JsonIgnore
    public void removeRequest(UUID target) {
        friendRequestDatas.removeIf(e -> e.getSender().equals(target.toString()) || e.getTarget().equals(target.toString()));
    }
}
