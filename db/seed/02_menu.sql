-- M3/M4 (owner: Pasha) - the demo menu: 4 categories and 16 items (same as the Phase 1
-- in-memory data, so item ids 1..16 are the same). Prices in Iraqi dinars.
-- stock_left starts equal to daily_stock; NULL = unlimited.
INSERT INTO categories (name, sort_order) VALUES
  ('Hot meals', 1),
  ('Sandwiches', 2),
  ('Drinks', 3),
  ('Desserts', 4);

INSERT INTO menu_items (category_id, name, description, price_iqd, vegetarian, daily_stock, stock_left) VALUES
  ((SELECT id FROM categories WHERE name = 'Hot meals'),  'Chicken biryani',      'Spiced rice with chicken, almonds and raisins',  5000, FALSE, 30,   30),
  ((SELECT id FROM categories WHERE name = 'Hot meals'),  'Quzi lamb and rice',   'Slow-cooked lamb over spiced rice',              7000, FALSE, 15,   15),
  ((SELECT id FROM categories WHERE name = 'Hot meals'),  'Kubba (2 pieces)',     'Fried bulgur shells filled with minced meat',    3000, FALSE, NULL, NULL),
  ((SELECT id FROM categories WHERE name = 'Hot meals'),  'Lentil soup',          'Red lentil soup with lemon',                     1500, TRUE,  NULL, NULL),
  ((SELECT id FROM categories WHERE name = 'Hot meals'),  'Vegetable dolma',      'Vine leaves and vegetables stuffed with rice',   4000, TRUE,  20,   20),
  ((SELECT id FROM categories WHERE name = 'Sandwiches'), 'Falafel wrap',         'Falafel, salad and tahini in flat bread',        2500, TRUE,  NULL, NULL),
  ((SELECT id FROM categories WHERE name = 'Sandwiches'), 'Chicken shawarma',     'Chicken shawarma with garlic sauce and pickles', 3500, FALSE, 40,   40),
  ((SELECT id FROM categories WHERE name = 'Sandwiches'), 'Kebab sandwich',       'Grilled minced lamb kebab in samoon bread',      4000, FALSE, 0,    0),
  ((SELECT id FROM categories WHERE name = 'Sandwiches'), 'Cheese manakish',      'Flat bread baked with white cheese',             2000, TRUE,  NULL, NULL),
  ((SELECT id FROM categories WHERE name = 'Drinks'),     'Ayran',                'Cold salted yoghurt drink',                       750, TRUE,  NULL, NULL),
  ((SELECT id FROM categories WHERE name = 'Drinks'),     'Black tea',            'Iraqi tea in an istikan glass',                   500, TRUE,  NULL, NULL),
  ((SELECT id FROM categories WHERE name = 'Drinks'),     'Fresh orange juice',   'Freshly squeezed orange juice',                  2000, TRUE,  25,   25),
  ((SELECT id FROM categories WHERE name = 'Drinks'),     'Bottled water',        '500 ml bottle',                                   500, TRUE,  NULL, NULL),
  ((SELECT id FROM categories WHERE name = 'Desserts'),   'Baklava (3 pieces)',   'Filo pastry with pistachio and syrup',           2000, TRUE,  20,   20),
  ((SELECT id FROM categories WHERE name = 'Desserts'),   'Kleicha date cookies', 'Traditional cookies filled with dates',          1500, TRUE,  NULL, NULL),
  ((SELECT id FROM categories WHERE name = 'Desserts'),   'Zalabia',              'Crispy fried dough in syrup',                    1500, TRUE,  10,   10);
