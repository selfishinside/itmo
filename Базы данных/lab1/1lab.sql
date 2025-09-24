BEGIN;

CREATE TABLE "Событие" (
    "id" SERIAL PRIMARY KEY,
    "Тип_события" TEXT NOT NULL,
    "Описание" TEXT NOT NULL,
    "Дата" DATE NOT NULL
);

CREATE TABLE "Корабль" (
    "id" SERIAL PRIMARY KEY,
    "Название" TEXT NOT NULL,
    "Состояние" TEXT NOT NULL,
    "Статус_коммуникации" TEXT NOT NULL,
    "Задание" INTEGER REFERENCES "Событие"("id")
);

CREATE TABLE "Человек" (
    "id" SERIAL PRIMARY KEY,
    "ФИО" TEXT NOT NULL,
    "Пол" VARCHAR (10) CHECK ("Пол" IN ('Мужской', 'Женский')) NOT NULL,
    "Дата_рождения" DATE NOT NULL
);

CREATE TABLE "Профессия" (
    "id" SERIAL PRIMARY KEY,
    "Название" TEXT NOT NULL UNIQUE
);

CREATE TABLE "ООН" (
    "id" SERIAL PRIMARY KEY,
    "Страны" TEXT NOT NULL,
    "Тема_переговоров" INTEGER REFERENCES "Событие"("id"),
    "Генсек" TEXT NOT NULL
);

CREATE TABLE "Локация" (
    "id" SERIAL PRIMARY KEY,
    "Погода" TEXT NOT NULL,
    "Название" TEXT NOT NULL,
    "Координата_X" INTEGER NOT NULL,
    "Координата_Y" INTEGER NOT NULL,
    "Координата_Z" INTEGER NOT NULL,
    UNIQUE ("Координата_X", "Координата_Y", "Координата_Z")
);

CREATE TABLE "НЛО" (
    "id" SERIAL PRIMARY KEY,
    "Имя" TEXT NOT NULL,
    "Отношение_к_земле" TEXT NOT NULL,
    "Возможность_контакта_с_землянами" BOOLEAN NOT NULL
);

CREATE TABLE "МотивыНЛО" (
    "id" SERIAL PRIMARY KEY,
    "Предполагаемая_цель" TEXT NOT NULL
);

CREATE TABLE "НЛО__МотивыНЛО" (
    "НЛО_Id" INTEGER NOT NULL REFERENCES "НЛО"("id"),
    "МотивыНЛО_id" INTEGER NOT NULL REFERENCES "МотивыНЛО"("id"),
    "Процент_выполнения_цели" INTEGER CHECK ("Процент_выполнения_цели" <= 100 AND "Процент_выполнения_цели" >= 0) NOT NULL,
    "Настрой" TEXT NOT NULL,
    PRIMARY KEY ("НЛО_Id", "МотивыНЛО_id")
);

CREATE TABLE "НЛО_Локация" (
    "НЛО_Id" INTEGER NOT NULL REFERENCES "НЛО"("id"),
    "Локация_Id" INTEGER NOT NULL REFERENCES "Локация"("id"),
    PRIMARY KEY ("НЛО_Id", "Локация_Id")
);

CREATE TABLE "Корабль_Локация" (
    "Корабль_Id" INTEGER NOT NULL REFERENCES "Корабль"("id"),
    "Локация_Id" INTEGER NOT NULL REFERENCES "Локация"("id"),
    PRIMARY KEY ("Корабль_Id", "Локация_Id")
);

CREATE TABLE "Корабль_Человек" (
    "Корабль_Id" INTEGER NOT NULL REFERENCES "Корабль"("id"),
    "Человек_Id" INTEGER NOT NULL REFERENCES "Человек"("id"),
    PRIMARY KEY ("Корабль_Id", "Человек_Id")
);

CREATE TABLE "Человек_Профессия" (
    "Человек_Id" INTEGER NOT NULL REFERENCES "Человек"("id"),
    "Профессия_Id" INTEGER NOT NULL REFERENCES "Профессия"("id"),
    "Место_работы" TEXT NOT NULL,
    "Дата_начала_работы" DATE NOT NULL,
    "Место_обучения" TEXT
);


INSERT INTO "Событие" ("Тип_события", "Описание", "Дата") VALUES
('Переговоры', 'Переговоры на тему контакта', '2023-10-15'),
('Запуск', 'Запуск нового спутника', '2023-11-20');

INSERT INTO "Корабль" ("Название", "Состояние", "Статус_коммуникации", "Задание") VALUES
('Леонов', 'Активный', 'Открытый', 1),
('Вояджер', 'Запуск','Открытый', 2);

INSERT INTO "Человек" ("ФИО", "Пол", "Дата_рождения") VALUES
('Иван Иванов Степанов', 'Мужской', '1980-04-12'),
('Мария Петрова Александровна', 'Женский', '1985-07-24');

INSERT INTO "Профессия" ("Название") VALUES
('Инженер'),
('Биолог');

INSERT INTO "ООН" ("Страны", "Тема_переговоров", "Генсек") VALUES
('Россия, США', 1, 'Антонио Гутерреш');

INSERT INTO "Локация" ("Погода", "Название", "Координата_X", "Координата_Y", "Координата_Z") VALUES
('Солнечно', 'Москва', 1, 2, 3000),
('Облачно', 'Нью-Йорк', 2, 3, 40000),
('Облочно', 'байканур', 2, 3, 5234);

INSERT INTO "НЛО" ("Имя", "Отношение_к_земле", "Возможность_контакта_с_землянами") VALUES
('Альфа', 'Дружелюбное', TRUE),
('Бета', 'Враждебное', FALSE);

INSERT INTO "МотивыНЛО" ("Предполагаемая_цель") VALUES
('Исследование'),
('Колонизация');

INSERT INTO "НЛО__МотивыНЛО" ("НЛО_Id", "МотивыНЛО_id", "Процент_выполнения_цели", "Настрой") VALUES
(1, 1, 50, 'Позитивный'),
(2, 2, 20, 'Негативный');

INSERT INTO "НЛО_Локация" ("НЛО_Id", "Локация_Id") VALUES
(1, 1),
(2, 2);

INSERT INTO "Корабль_Локация" ("Корабль_Id", "Локация_Id") VALUES
(1, 1),
(2, 3);

INSERT INTO "Корабль_Человек" ("Корабль_Id", "Человек_Id") VALUES
(1, 1),
(1, 2);

INSERT INTO "Человек_Профессия" ("Человек_Id", "Профессия_Id", "Место_работы", "Дата_начала_работы", "Место_обучения") VALUES
(1, 1, 'Роскосмос', '2020-05-01', 'МГУ'), 
(2, 2, 'Институт биологии', '2021-09-01', 'ИМТО'); 

COMMIT;
