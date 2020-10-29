package cz.cvut.dsv.tomenyev.network;

import cz.cvut.dsv.tomenyev.utils.Log;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Node {

    private final Address address;

    private final Log logger;

    private Address next;

    private Address prev;

    private Address leader;

}
