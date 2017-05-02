package com.dce.grabtutor.Model;

/**
 * Created by Skye on 4/25/2017.
 */

public class URI {

    public static final String HOST = "http://192.168.254.104/grabtutor/";

    public static final String TOKEN_UPDATE = HOST + "token_update.php";
    public static final String TOKEN_REMOVE = HOST + "token_remove";

    public static final String LOGIN = HOST + "login.php";
    public static final String LOGIN_ADMIN = HOST + "login_admin.php";
    public static final String SIGNUP = HOST + "signup.php";

    public static final String CONVERSATION_LOAD = HOST + "conversation_load.php";
    public static final String MESSAGE_LOAD = HOST + "message_load.php";
    public static final String MESSAGE_SEND = HOST + "message_send.php";

    public static final String SCHEDULE_ADD = HOST + "schedule_add.php";
    public static final String SCHEDULE_REMOVE = HOST + "schedule_remove.php";
    public static final String SCHEDULE_LOAD = HOST + "schedule_load.php";
    public static final String SCHEDULE_LOAD_DAY = HOST + "schedule_load_day.php";

    public static final String APTITUDE_START = HOST + "aptitude_start.php";
    public static final String APTITUDE_EXAM = HOST + "aptitude_exam.php";
    public static final String APTITUDE_STATUS_CHECK = HOST + "aptitude_status_check.php";

    public static final String USER_LOCATION_UPDATE = HOST + "user_location_update.php";

    public static final String TUTOR_SEARCH = HOST + "tutor_search.php";
}
