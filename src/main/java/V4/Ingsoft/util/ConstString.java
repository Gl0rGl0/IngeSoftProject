package V4.Ingsoft.util;

public class ConstString {
    // --- RUNNING mode commands ---
    public static final String RUN_ADD_INFO = """
            add [-c] [-f] [-v] [String: username] [String: psw]
                -c        Adds a configurator
                -f        Adds a fruitore (visitor/user)
                username  Specifies the username of the person being added
                psw       Specifies the password of the person being added

            add [-t] [String: title] [String: description] [GPS: meetingPlace] [Date: initDay] [Date: finishDay] [Time: initTime] [int: duration] [boolean: free] [int: numMinPartecipants] [int: numMaxPartecipants] [StatusLuoghi: stato]
                -T                  Adds a visit type
                title               The title of the visit type
                description         A short description of the visit type
                meetingPlace        The meeting point for the visit type [latitude,longitude]
                initDay             Start date of the visit type period [YYYY-MM-DD]
                finishDay           End date of the visit type period [YYYY-MM-DD]
                initTime            The start time of the visit type [HH:MM]
                duration            Duration of the visit type in minutes
                free                Indicates if the visit type is free [true/false]
                numMinPartecipants  Minimum number of participants (fruitori)
                numMaxPartecipants  Maximum number of participants (fruitori)
                stato               State of the visit type [PROPOSED/CONFIRMED/CANCELLED/PERFORMED] (Note: Enum names might differ)

            To leave a field empty, type "/"
            """;

    public static final String RUN_ADD_LINE_INFO = "Adds a Person/VisitType/Place to the database";

    public static final String REMOVE_INFO = """
            remove [-c] [-f] [-v] [String: username]
                -c        Removes a configurator
                -f        Removes a fruitore (visitor/user)
                -v        Removes a volunteer
                username  Specifies the username of the person to remove

            remove [-V] [-L] [String: title]
                -V        Removes a visit
                -L        Removes a place
                title     Specifies the title of the visit or place to remove
            """;

    public static final String REMOVE_LINE_INFO = "Removes a Person/Visit/Place from the database";

    public static final String LOGIN_INFO = """
            login [String: username] [String: password]
                username  Specifies the username to log in with
                password  Specifies the password to log in with
            """;

    public static final String LOGIN_LINE_INFO = "Log in by entering credentials";

    public static final String LOGOUT_INFO = """
            logout
            """;

    public static final String LOGOUT_LINE_INFO = "Log out from the system";

    public static final String CHANGEPSW_INFO = """
            changepsw [String: newpassword]
                newpassword  Specifies the new password for the account
            """;

    public static final String CHANGEPSW_LINE_INFO = "Change the password";

    public static final String TIME_INFO = """
                time [[-d] [-m] [-a]] [int: amount]
                    amount  Specifies the number of units to jump
                    optionally -d: number of days
                               -m: number of months
                               -a: number of years
                time -s [Date dd/mm/yy]
                    Sets the current date to dd/mm/yy
                time
                    Shows the current date
            """;

    public static final String TIME_LINE_INFO = "System date management";

    public static final String ASSIGN_INFO = """
            assign [-L] [String: placeTitle] [String: visitTitle]
                placeTitle     Title of the place to assign the visit type to
                visitTitle     Title of the visit to assign
            assign [-V] [String: visitTitle] [String: volunteerUsername]
                visitTitle        Title of the visit to assign the volunteer to
                volunteerUsername Username of the volunteer to assign
            """;

    public static final String ASSIGN_LINE_INFO = "Assigns a visit to a place or a volunteer to a visit";

    public static final String LIST_INFO = """
                list [-l] [-v]
                    -l(L):  List of visitable places
                    -v:     List of available volunteers
                list [-V] [[-a] [-p] [-c] [-C] [-e]]
                    -V: List of available visits
                        Optionally ([-p] default):
                            [-a]  All, all visits (including past ones)
                            [-p]  List of Proposed visits
                            [-c]  List of Completed visits
                            [-C]  List of Cancelled visits
                            [-e]  List of Performed visits (past)
            """;

    public static final String LIST_LINE_INFO = "Displays the desired list";

    public static final String PRECLUDE_INFO = """

            """;

    public static final String PRECLUDE_LINE_INFO = "Adds/removes precluded date";

    public static final String EXIT_INFO = """
            exit
            """;

    public static final String EXIT_LINE_INFO = "Closes the program";

    public static final String VISIT_INFO = """
            visit [[-a] [-r]] [String: username_Fruitore] [String: visit_title] [Date: visit_date]
                [-a]                Adds a fruitore (visitor/user) to the selected visit
                [-r]                Removes a fruitore from the selected visit
                username_Fruitore   Username of the fruitore to add/remove
                visit_title         Title of the visit to operate on
                visit_date          Date of the visit to operate on
            """;
    public static final String VISIT_LINE_INFO = "Register a fruitore (visitor/user) for a visit";

    public static final String MYVISIT_INFO = """

            """;
    public static final String MYVISIT_LINE_INFO = "Displays visits associated with the current user";

    // --- SETUP mode commands ---
    public static final String SETUP_ADD_INFO = """
            add [-L] [String: name] [String: description] [GPS: position]
                -L            Adds a place
                name          Name of the place
                description   Short description of the place
                position      GPS position [latitude,longitude]
            """;

    public static final String SETUP_ADD_LINE_INFO = "Adds a Place to the database";

    // LOGIN, LOGOUT, CHANGEPSW, and TIME are identical to those in RUNNING mode

    public static final String SETMAX_INFO = """
            setmax [int: max]
                max     Specifies the maximum number of participants (fruitori) for a visit
            """;

    public static final String SETMAX_LINE_INFO = "(SETUP) Assigns the maximum value for visits";

    public static final String SETAMBITO_INFO = """
            setambito [String: scopeName]
                scopeName     Specifies the territorial scope of the program
            """;

    public static final String SETAMBITO_LINE_INFO = "(SETUP) Assigns the name of the territory";

    public static final String DONE_INFO = """
            done
            """;

    public static final String DONE_LINE_INFO = "Ends the setup phase";

    public static final String HELP_INFO = """
            help
            """;

    public static final String HELP_LINE_INFO = "Provides information about available commands.";

    public static final String AVAILABILITY_INFO = """
            setav [-a | -r ] [Date: dateAvailable] [Date...]
                -a              Add a date
                -r              Remove a date
                dateAvailable   Date to set the availability. Format <GG/MM/AAAA>
            You can add/remove multiple date
            """;
    public static final String AVAILABILITY_LINE_INFO = "Add or remove at least one availability date from your calendar";


    public static final String MAKEPLAN_INFO = """
            makeplan
            """;
    public static final String MAKEPLAN_LINE_INFO = "Start the generation of the plan visits by the System";

    public static final String COLLECTIONMANAGER_INFO = """
            collection [-o | -c ]
                -o              Open the collection
                -c              Close the collection
            """;
    public static final String COLLECTIONMANAGER_LINE_INFO = "Manage the collection status of Volunteer Availability";
}
