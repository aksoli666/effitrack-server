# EffiTrack Server API

The developed RESTful API is a comprehensive server-side solution for the digitalization of manufacturing processes and equipment efficiency monitoring. 
The system architecture is built on principles of security and responsibility separation using JWT authorization to differentiate access rights between Master and Operator roles.

The system provides a full asset management lifecycle from registration to real-time status monitoring. 
It enables tracking of Work Idle and Setup statuses with detailed event logging for inefficiency root cause analysis. Functionality includes equipment search by inventory number and a mechanism for assigning specific machines to employees.

The Task Management module allows scheduling maintenance and recording task completion for productivity metrics calculation. The API includes an IoT simulation module for industrial controller signals to imitate emergency stops or starts without physical hardware connection. The system automatically aggregates weekly data generates performance charts and sends reports to managers via email.

The technology stack includes Java 17 Spring Boot 3 Hibernate MySQL Spring Security and Java Mail Sender.

---

Розроблений RESTful API являє собою комплексне серверне рішення для цифровізації виробничих процесів та моніторингу ефективності обладнання. Архітектура системи побудована на принципах безпеки та розподілу відповідальності з використанням JWT-авторизації для розмежування прав доступу між ролями Майстер та Оператор.

Система забезпечує повний життєвий цикл обліку активів від реєстрації до моніторингу поточного стану в реальному часі. Реалізована можливість відстеження статусів Робота Простій та Наладка зі збереженням детальної історії подій для аналізу причин неефективності. Функціонал включає пошук обладнання за інвентарним номером та механізм закріплення верстатів за конкретними співробітниками.

Модуль Task Management дозволяє призначати планові роботи та фіксувати факт їх виконання для розрахунку показників продуктивності. API містить модуль IoT-симуляції сигналів промислових контролерів для імітації аварійних зупинок або запусків без фізичного підключення до техніки. Система автоматично агрегує дані за тиждень генерує графіки та надсилає звіти керівникам електронною поштою.

Технологічний стек включає Java 17 Spring Boot 3 Hibernate MySQL Spring Security та Java Mail Sender.
