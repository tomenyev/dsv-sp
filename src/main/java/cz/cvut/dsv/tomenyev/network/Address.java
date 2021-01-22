package cz.cvut.dsv.tomenyev.network;

import cz.cvut.dsv.tomenyev.utils.Constant;
import cz.cvut.dsv.tomenyev.utils.Log;
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
/**
 * Network node unique id. Consists of ip and port. eg. 127.0.0.1:8080
 */
public class Address implements Serializable {

    private final String ip;

    private final short port;

    public Address(String address) throws Exception {
        String[] strs = address.split(":");

        if(strs.length < 2) {
            Log.getInstance().print(Log.To.CONSOLE, Constant.BAD_ADDRESS_FORMAT);
            throw new Exception();
        }

        try {
            this.port = Short.parseShort(strs[1]);
        } catch (Exception ignored) {
            Log.getInstance().print(Log.To.CONSOLE, Constant.BAD_ADDRESS_FORMAT);
            throw new Exception();
        }

        this.ip = strs[0];

//        if(!InetAddressValidator.getInstance().isValid(this.ip)) {
//            Log.getInstance().print(Log.To.CONSOLE, Constant.BAD_ADDRESS_FORMAT);
//            throw new Exception();
//        }
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
