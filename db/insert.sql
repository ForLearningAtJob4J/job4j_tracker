TRUNCATE TABLE items;

SELECT setval('public.items_id_seq', 1, true);

INSERT INTO
    items (name)
VALUES
    ('item #1'),
    ('item #2'),
    ('item #3'),
    ('item #4'),
    ('item #5'),
    ('item #6');