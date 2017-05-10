package com.dce.grabtutor.Model;

/**
 * Created by Skye on 4/25/2017.
 */

public class URI {

    public static final String HOST = "http://192.168.1.16/grabtutor/";

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
    public static final String SCHEDULE_REMOVE_BOOKED = HOST + "schedule_remove_booked.php";
    public static final String SCHEDULE_LOAD = HOST + "schedule_load.php";
    public static final String SCHEDULE_LOAD_STUDENT = HOST + "schedule_load_student.php";

    //    public static final String APTITUDE_START = HOST + "aptitude_start.php";
    public static final String APTITUDE_EXAM = HOST + "aptitude_exam.php";
    public static final String APTITUDE_STATUS_CHECK = HOST + "aptitude_status_check.php";

    public static final String USER_LOCATION_UPDATE = HOST + "user_location_update.php";
    public static final String USER_SEARCH_DISTANCE_UPDATE = HOST + "user_search_distance_update.php";

    public static final String TUTOR_SEARCH = HOST + "tutor_search.php";
    public static final String TUTOR_BOOKING = HOST + "tutor_booking.php";

    public static final String TUTOR_SUBJECT_UPDATE = HOST + "tutor_subject_update.php";
    public static final String TUTOR_SUBJECT_LOAD = HOST + "tutor_subject_load.php";

    public static final String TUTOR_REQUEST_LOAD = HOST + "tutor_request_load.php";
    public static final String TUTOR_REQUEST_APPROVE = HOST + "tutor_request_approve.php";
    public static final String TUTOR_REQUEST_DECLINE = HOST + "tutor_request_decline.php";

    public static final String TUTOR_UPLOAD = HOST + "tutor_upload.php";
    public static final String TUTOR_UPLOAD_REQUEST = HOST + "tutor_file_upload.php";
}
