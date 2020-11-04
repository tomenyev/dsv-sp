package cz.cvut.dsv.tomenyev.utils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Command {

    INIT_NETWORK,
    JOIN_NETWORK,
    PRINT_STATUS,
    PRINT_LOG,
    SEND_MESSAGE,
    INIT_ELECTION,
    QUIT,
    FIX,
    FORCE_QUIT,
    DEFAULT,
    HELP;

    public static Command convert(String str) {
        if(str == null)
            return DEFAULT;

        switch (str.toUpperCase().trim()) {
            case "IN":
                return INIT_NETWORK;
            case "JN":
                return JOIN_NETWORK;
            case "IE":
                return INIT_ELECTION;
            case "S":
                return PRINT_STATUS;
            case "L":
                return PRINT_LOG;
            case "M":
                return SEND_MESSAGE;
            case "Q":
                return QUIT;
            case "FQ":
                return FORCE_QUIT;
            case "H":
                return HELP;
            case "F":
                return FIX;
            default:
                return DEFAULT;
        }
    }
}
