package com.minimob.adserving.helpers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MinimobViewCommandParser
{

    private final static String TAG = MinimobViewCommandParser.class.getSimpleName();

    public Map<String, String> parseCommandUrl(String commandUrl)
    {
        // The command is a URL string that looks like this:
        //
        // mmji://command?param1=val1&param2=val2&...
        //
        // We need to parse out the command and create a map containing it and
        // its the parameters and their associated values.

        MinimobViewLog.d(TAG, "parseCommandUrl " + commandUrl);

        // Remove mmji:// prefix.
        String s = commandUrl.substring("mmji://".length());

        String command;
        Map<String, String> params = new HashMap<>();

        int idx = s.indexOf('?');
        // if there are any parameters, parse them
        if (idx != -1)
        {
            // the command is the string before the parameters' index
            command = s.substring(0, idx);
            String paramStr = s.substring(idx + 1);
            String[] paramArray = paramStr.split("&");
            for (String param : paramArray)
            {
                idx = param.indexOf('=');
                String key = param.substring(0, idx);
                String val = param.substring(idx + 1);
                params.put(key, val);
            }
        }
        else
        {
            command = s;
        }

        // Check for valid command.
        if (!isValidCommand(command))
        {
            MinimobViewLog.w(TAG + "-" + "command " + command + " is unknown");
            return null;
        }

        // Check for valid parameters for the given command.
        if (!checkParamsForCommand(command, params))
        {
            MinimobViewLog.w(TAG + "-" + "command URL " + commandUrl + " is missing parameters");
            return null;
        }

        Map<String, String> commandMap = new HashMap<>();
        commandMap.put("command", command);
        commandMap.putAll(params);
        return commandMap;
    }

    private boolean isValidCommand(String command)
    {
        final String[] commands = {
                "close",
                "adsReady",
                "noAds",
                "expand",
        };
        return (Arrays.asList(commands).contains(command));
    }

    private boolean checkParamsForCommand(String command, Map<String, String> params)
    {
        if (command.equals("adsReady"))
        {
            return params.containsKey("packageId");
        }
        return true;
    }
}
