package cn.charlotte.pit.network;

import cn.charlotte.pit.data.mail.Mail;
import cn.charlotte.pit.util.pidgin.packet.Packet;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;

import java.util.UUID;

/**
 * @Author: EmptyIrony
 * @Date: 2021/3/16 22:59
 */
public class MailSend01Packet extends Packet {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private UUID uuid;
    private Mail mail;

    public MailSend01Packet() {
    }

    @Override
    public int id() {
        return 1;
    }

    @Override
    @SneakyThrows
    public JsonObject serialize() {
        final JsonObject json = new JsonObject();
        json.addProperty("uuid", uuid.toString());
        final String value = MAPPER.writeValueAsString(mail);
        json.addProperty("mail", value);

        return json;
    }

    @Override
    @SneakyThrows
    public void deserialize(JsonObject object) {
        this.uuid = UUID.fromString(object.get("uuid").getAsString());
        final String mail = object.get("mail").getAsString();
        this.mail = MAPPER.readValue(mail, Mail.class);
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public Mail getMail() {
        return this.mail;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setMail(Mail mail) {
        this.mail = mail;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof MailSend01Packet)) return false;
        final MailSend01Packet other = (MailSend01Packet) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$uuid = this.getUuid();
        final Object other$uuid = other.getUuid();
        if (this$uuid == null ? other$uuid != null : !this$uuid.equals(other$uuid)) return false;
        final Object this$mail = this.getMail();
        final Object other$mail = other.getMail();
        if (this$mail == null ? other$mail != null : !this$mail.equals(other$mail)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof MailSend01Packet;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $uuid = this.getUuid();
        result = result * PRIME + ($uuid == null ? 43 : $uuid.hashCode());
        final Object $mail = this.getMail();
        result = result * PRIME + ($mail == null ? 43 : $mail.hashCode());
        return result;
    }

    public String toString() {
        return "MailSend01Packet(uuid=" + this.getUuid() + ", mail=" + this.getMail() + ")";
    }
}
