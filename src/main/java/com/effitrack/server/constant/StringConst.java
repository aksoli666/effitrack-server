package com.effitrack.server.constant;

public class StringConst {
    // --- SYSTEM & FORMATS ---
    public static final String SHIFT = "-";
    public static final String SYMBOL_ASTERISK = "*";
    public static final String DATE_FORMAT = "dd.MM";
    public static final String FILE_NAME_SHIFT_CHART = "shift_chart.png";
    public static final String EMPTY_STRING = "";

    // --- MQTT CONFIG ---
    public static final String MQTT_BROKER_URL = "tcp://broker.emqx.io:1883";
    public static final String CLIENT_ID_PREFIX = "effitrack-server-";
    public static final String EQUIPMENT_UPDATE_TOPIC = "factory/equipment/update";
    public static final String INPUT_CHANNEL = "mqttInputChannel";
    public static final String BROKER_DESTINATION_PREFIX = "/topic";
    public static final String ALERT_TOPIC = BROKER_DESTINATION_PREFIX + "/alerts";
    public static final String APPLICATION_DESTINATION_PREFIX = "/app";
    public static final String WEBSOCKET_ENDPOINT = "/ws-connect";

    // --- SECURITY & AUTH ---
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String AUTHORITY_MASTER = "hasAuthority('MASTER')";
    public static final String AUTHORITY_OPERATOR = "hasAuthority('OPERATOR')";
    public static final String[] PUBLIC_ENDPOINTS = {
            "/api/auth/**", "/api/simulation/**", "/ws-connect/**",
            "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html"
    };
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_DELETE = "DELETE";
    public static final String METHOD_OPTIONS = "OPTIONS";

    // --- MASTER DEFAULT ---
    public static final String MASTER_FULL_NAME = "Головний Майстер";
    public static final String MASTER_TABLE_NUMBER = "9999";
    public static final String MASTER_PIN_CODE = "0000";
    public static final String MASTER_PROFESSION = "Оператор ЧПУ / 5 розряд";
    public static final String MASTER_SHOP_NUMBER = "Цех №5";
    public static final String MASTER_EMAIL = "mastre.effi.track@gmail.com";

    // --- API BASE URLS ---
    public static final String BASE_URL_AUTH = "/api/auth";
    public static final String BASE_URL_EQUIPMENT = "/api/equipment";
    public static final String BASE_URL_REPORTS = "/api/reports";
    public static final String BASE_URL_USERS = "/api/users";
    public static final String BASE_URL_TASKS = "/api/tasks";

    // --- ENDPOINTS ---
    public static final String ENDPOINT_REGISTER = "/register";
    public static final String ENDPOINT_REGISTER_BATCH = "/register/batch";
    public static final String ENDPOINT_LOGIN = "/login";
    public static final String ENDPOINT_EQUIPMENT_BATCH = "/batch";
    public static final String ENDPOINT_TASK_BATCH = "/batch/{userId}";
    public static final String ENDPOINT_SEARCH = "/search";
    public static final String ENDPOINT_BY_ID = "/{id}";
    public static final String ENDPOINT_STATUS_UPDATE = "/{id}/status";
    public static final String ENDPOINT_USER_PROFILE = "/{userId}";
    public static final String ENDPOINT_USER_EQUIPMENT = "/{userId}/equipment";
    public static final String ENDPOINT_USER_TASKS = "/user/{userId}";
    public static final String ENDPOINT_START_TASK = "/{taskId}/start";
    public static final String ENDPOINT_COMPLETE_TASK = "/{taskId}/complete";
    public static final String ENDPOINT_TASK_UPDATE = "/{taskId}"; // New
    public static final String ENDPOINT_SEND_REPORT = "/send/{userId}";
    public static final String ENDPOINT_REPORT_EQUIPMENT = "/equipment/{userId}"; // New

    // --- PARAMS ---
    public static final String PARAM_INVENTORY = "inv";
    public static final String VAR_USER_ID = "userId";

    // --- MESSAGES ---
    public static final String SUCCESS_REGISTER = "Реєстрація успішна!";
    public static final String SUCCESS_BATCH_REGISTER = "Всі користувачі додані!";
    public static final String SUCCESS_MESSAGE = "Звіт надіслано!";
    public static final String SUCCESS_EQUIPMENT_ADDED = "Обладнання додано!";
    public static final String SUCCESS_STATUS_UPDATED = "Статус оновлено";
    public static final String SUCCESS_REPORT_EQUIPMENT = "Звіт по обладнанню надіслано";
    public static final String ERROR_TABLE_TAKEN = "Помилка: Табельний номер вже використовується";
    public static final String ERROR_NOT_FOUND = "Помилка: Юзера або Верстат не знайдено";
    public static final String ERROR_PREFIX_OBJ_NOT_FOUND = "Об'єкт не знайдено: ";
    public static final String ERROR_MSG_BAD_CREDENTIALS = "Невірний Табельний номер або ПІН-код";
    public static final String ERROR_PREFIX_SERVER = "Помилка сервера: ";

    // --- EMAIL TEMPLATES ---
    public static final String SUBJECT_TEMPLATE = "Звіт за зміну: %s (%s)";
    public static final String SUBJECT_EQUIPMENT_REPORT_TEMPLATE = "Звіт по обладнанню: %s (%s)";
    public static final String BODY_TEMPLATE = "Оператор: %s\nЦех: %s\n\nЗвіт згенеровано автоматично системою EffiTrack.";
    public static final String MSG_SUCCESS_TEMPLATE = "✅ Статус оновлено: %s";
    public static final String MSG_NOT_FOUND_TEMPLATE = "⚠️ Верстат не знайдено: %s";
    public static final String MSG_ERROR_PREFIX = "❌ Помилка: ";
    public static final String USER_NOT_FOUND_TEMPLATE_INV = "Користувача з табельним номером %s не знайдено";

    // --- CHART KEYS ---
    public static final String CHART_TITLE_TASKS_PREFIX = "Продуктивність за тиждень";
    public static final String ROW_KEY_DONE = "Виконано";
    public static final String ROW_KEY_IN_PROGRESS = "В процесі";
    public static final String ROW_KEY_ASSIGNED = "Призначено";
    public static final String CHART_TITLE_EFFICIENCY = "Ефективність за зміну";
    public static final String CHART_LABEL_NO_DATA = "Немає даних";

    // --- SWAGGER INFO ---
    public static final String SWAGGER_TITLE = "EffiTrack API";
    public static final String SWAGGER_VERSION = "1.0.0";
    public static final String SWAGGER_DESCRIPTION = "Документація API для системи моніторингу виробництва";
    public static final String SWAGGER_AUTH_SCHEME_NAME = "Bearer Authentication";
    public static final String SWAGGER_AUTH_SCHEME = "bearer";
    public static final String SWAGGER_AUTH_FORMAT = "JWT";

    // --- SWAGGER TAGS ---
    public static final String TAG_AUTH = "Авторизація";
    public static final String TAG_AUTH_DESC = "Реєстрація та вхід у систему (JWT)";
    public static final String TAG_EQUIPMENT = "Обладнання";
    public static final String TAG_EQUIPMENT_DESC = "Управління верстатами, пошук та історія";
    public static final String TAG_USERS = "Користувачі";
    public static final String TAG_USERS_DESC = "Профіль оператора та управління списком 'Моє обладнання'";
    public static final String TAG_TASKS = "Завдання (To-Do)";
    public static final String TAG_TASKS_DESC = "Планування робіт та відмітка про виконання";
    public static final String TAG_REPORTS = "Звіти";
    public static final String TAG_REPORTS_DESC = "Генерація графіків та відправка на пошту";

    // --- OPERATION SUMMARIES ---
    public static final String OP_REGISTER_SUM = "Реєстрація користувача";
    public static final String OP_REGISTER_DESC = "Створює нового користувача. Якщо табельний номер зайнятий - повертає помилку.";
    public static final String OP_REGISTER_BATCH_SUM = "Масова реєстрація";
    public static final String OP_LOGIN_SUM = "Вхід у систему";
    public static final String OP_LOGIN_DESC = "Перевіряє табельний номер та ПІН-код. Повертає JWT токен.";
    public static final String OP_EQ_ALL_SUM = "Отримати все обладнання";
    public static final String OP_EQ_CREATE_SUM = "Створити верстат (Адмін)";
    public static final String OP_EQ_BATCH_CREATE_SUM = "Масове створення обладнання";
    public static final String OP_EQ_SEARCH_SUM = "Пошук за інвентарним номером";
    public static final String OP_EQ_SEARCH_DESC = "Використовується для OCR сканера. Повертає 404, якщо не знайдено.";
    public static final String OP_EQ_ID_SUM = "Деталі обладнання";
    public static final String OP_EQ_ID_DESC = "Повертає повну інформацію про верстат за ID (включно з датами ТО).";
    public static final String OP_EQ_STATUS_SUM = "Змінити статус";
    public static final String OP_EQ_STATUS_DESC = "Оператор змінює статус (Простій/Робота) та вказує причину.";
    public static final String OP_USER_PROFILE_SUM = "Профіль користувача";
    public static final String OP_USER_PROFILE_DESC = "Отримати ПІБ, цех та роль за ID.";
    public static final String OP_USER_MY_EQ_SUM = "Моє обладнання";
    public static final String OP_USER_MY_EQ_DESC = "Список верстатів, закріплених за оператором.";
    public static final String OP_USER_ADD_EQ_SUM = "Додати до списку";
    public static final String OP_USER_ADD_EQ_DESC = "Прив'язує верстат до оператора (після сканування).";
    public static final String OP_TASK_LIST_SUM = "Список завдань";
    public static final String OP_TASK_LIST_DESC = "Отримати To-Do лист для конкретного оператора.";
    public static final String OP_TASK_CREATE_SUM = "Створити завдання";
    public static final String OP_TASK_BATCH_CREATE_SUM = "Масове створення завдань";
    public static final String OP_TASK_START_SUM = "Почати виконувати завдання";
    public static final String OP_TASK_START_DESC = "Ставить 'В прогресі'";
    public static final String OP_TASK_COMPLETE_SUM = "Виконати завдання";
    public static final String OP_TASK_COMPLETE_DESC = "Ставить 'Виконано' (isCompleted = true).";
    public static final String OP_TASK_UPDATE_SUM = "Оновити деталі завдання";
    public static final String OP_REPORT_SEND_SUM = "Надіслати звіт за зміну";
    public static final String OP_REPORT_SEND_DESC = "Генерує графік ефективності та надсилає лист майстру.";
    public static final String OP_REPORT_EQUIPMENT_SUM = "Надіслати звіт по обладнанню";

    // --- HTML & CSS GENERATION ---
    public static final String NEW_LINE = "\n";
    public static final String HTML_BR = "<br/>";
    public static final String HTML_BODY_START = "<html><body>";
    public static final String HTML_BODY_END = "</body></html>";

    // -- Task Report HTML --
    public static final String MSG_NO_TASKS = "<p><i>Завдань за цей період немає.</i></p>";
    public static final String HTML_HEADER_TASKS = "<h3>Деталізація завдань:</h3>";
    public static final String TEXT_HEADER_TITLE = "Назва завдання";
    public static final String TEXT_HEADER_STATUS = "Статус";
    public static final String TEXT_HEADER_TIME = "Час (факт)";
    public static final String TEXT_STATUS_DONE = "Виконано";
    public static final String TEXT_STATUS_IN_PROGRESS = "В роботі";
    public static final String TEXT_TIME_SUFFIX = " хв.";

    // -- Equipment Report HTML --
    public static final String HTML_H2_EQUIPMENT_REPORT = "<h2>Звіт ефективності обладнання</h2>";
    public static final String HTML_P_OPERATOR_FMT = "<p>Оператор: <b>%s</b></p>";
    public static final String HTML_P_SHOP_FMT = "<p>Цех: %s</p>";
    public static final String TEXT_HEADER_EQUIPMENT = "Обладнання";
    public static final String TEXT_HEADER_INV_NUM = "Інв. №";
    public static final String TEXT_HEADER_WORK_MIN = "Робота (хв)";
    public static final String TEXT_HEADER_DOWNTIME_MIN = "Простій (хв)";
    public static final String TEXT_HEADER_SETUP_MIN = "Налагодження (хв)";
    public static final String MSG_CHART_ATTACHED = "<p><i>Графік ефективності додано у вкладенні.</i></p>";

    // -- CSS Styles --
    public static final String STYLE_TABLE = "width: 100%; border-collapse: collapse; font-family: Arial, sans-serif;";
    public static final String STYLE_TABLE_MT20 = "width:100%; border-collapse: collapse; margin-top: 20px;";
    public static final String STYLE_ROW_HEADER = "background-color: #f2f2f2;";
    public static final String STYLE_CELL = "border: 1px solid #dddddd; text-align: left; padding: 8px;";
    public static final String STYLE_COLOR_PREFIX = "color:";
    public static final String COLOR_GREEN = "green";
    public static final String COLOR_RED = "#d9534f";

    // -- CSS for Equipment Report Cells --
    public static final String STYLE_CELL_GREEN_BOLD = "border: 1px solid #ddd; padding: 8px; color: green;";
    public static final String STYLE_CELL_RED_BOLD = "border: 1px solid #ddd; padding: 8px; color: red;";
    public static final String STYLE_CELL_ORANGE = "border: 1px solid #ddd; padding: 8px; color: orange;";

    // -- HTML Formats (Templates) --
    public static final String HTML_TABLE_START_FMT = "<table style=\"%s\">";
    public static final String HTML_TR_HEADER_FMT = "<tr style=\"%s\">";
    public static final String HTML_TH_FMT = "<th style=\"%s\">%s</th>";
    public static final String HTML_TD_FMT = "<td style=\"%s\">%s</td>";
    public static final String HTML_SPAN_STYLE_FMT = "<span style=\"%s\">%s</span>";
    public static final String HTML_BOLD_FMT = "<b>%s</b>";
    public static final String HTML_TH_STYLE = "border: 1px solid #ddd; padding: 8px;";
    public static final String HTML_TD_STYLE = "border: 1px solid #ddd; padding: 8px;";
}
