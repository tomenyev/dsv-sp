package cz.cvut.dsv.tomenyev.network;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.validator.routines.InetAddressValidator;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

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

    public boolean isGreaterThan(Address addr) throws UnknownHostException {
        return getIp().equals(addr.getIp()) ? isPortGreaterThan(addr) : isIpGreaterThan(addr);
    }

    public boolean isIpGreaterThan(Address addr) throws UnknownHostException {
        byte[] a = InetAddress.getByName(getIp()).getAddress();
        byte[] b = InetAddress.getByName(addr.getIp()).getAddress();
        for(int i = 0; i < 4; i++) {
            if (a[i] == b[i])
                continue;
            return a[i] > b[i];
        }
        return false;
    }

    public boolean isPortGreaterThan(Address addr) {
        return getPort() > addr.getPort();
    }

}
