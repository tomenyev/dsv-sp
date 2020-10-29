package cz.cvut.dsv.tomenyev.network;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.validator.routines.InetAddressValidator;

import java.io.Serializable;

@Getter
@ToString
@EqualsAndHashCode
public class Address implements Serializable {

    private final String ip;
    private final short port;

    public Address(String address) throws Exception {
        String[] strs = address.split(":");

        if(strs.length < 2) {
            throw new Exception();
            //TODO
        }

        try {
            this.port = Short.parseShort(strs[1]);
        } catch (Exception ignored) {
            throw new Exception();
            //TODO
        }

        this.ip = strs[0];

        if(!InetAddressValidator.getInstance().isValid(this.ip)) {
            throw new Exception();
            //TODO
        }
    }
}
