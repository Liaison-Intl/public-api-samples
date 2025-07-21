/**
 * Command-line argument parser.
 *
 * @version 1.1
 * @since 2020
 */
package com.liaisonedu.util;

import com.liaisonedu.constants.Constants;
import java.util.Optional;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

class CmdLine {

    private final Options options;
    private final CommandLine cmd;

    CmdLine(String[] args) throws ParseException {
        options = createOptions();
        CommandLineParser parser = new DefaultParser();
        cmd = parser.parse(options, args);
    }

    void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("unicas-api-sample-client", options);
    }

    private Options createOptions() {
        Options options = new Options();
        options.addOption(String.valueOf(Constants.OPT_HELP), "help", false,
                          "Print this message");
        options.addOption(String.valueOf(Constants.OPT_PRINT_PROPS), "show-properties", false,
                "Print all supported property keys");
        options.addOption(String.valueOf(Constants.OPT_PROPS), "properties", true,
                "Specify the path to a file overriding built-in properties");
        options.addOption(String.valueOf(Constants.OPT_OUTPUT), "output-location", true,
                "Specify the path to a directory where to store API response");
        options.addOption(String.valueOf(Constants.OPT_LIMIT), "application-limit", true,
                "Specify the max number of applications to fetch, -1 to get all. Default is 10.");
        return options;
    }

    Optional<String> getOption(char opt) {
        if (cmd.hasOption(opt)) {
            String optionValue = cmd.getOptionValue(opt);
            if (optionValue == null) {
                return Optional.of(String.valueOf(opt));
            } else {
                return Optional.of(optionValue);
            }
        } else {
            return Optional.empty();
        }
    }
}
