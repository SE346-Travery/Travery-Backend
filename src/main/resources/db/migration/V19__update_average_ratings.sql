-- =============================================
-- Travery Backend - Update Average Ratings & Counts
-- =============================================


UPDATE hotels SET 
    average_rating = COALESCE((SELECT AVG(average_rating) FROM reviews WHERE target_id = hotels.id AND target_type = 'HOTEL'), 0.0),
    review_count = COALESCE((SELECT COUNT(id) FROM reviews WHERE target_id = hotels.id AND target_type = 'HOTEL'), 0);

UPDATE tours SET 
    average_rating = COALESCE((SELECT AVG(average_rating) FROM reviews WHERE target_id = tours.id AND target_type = 'TOUR'), 0.0),
    review_count = COALESCE((SELECT COUNT(id) FROM reviews WHERE target_id = tours.id AND target_type = 'TOUR'), 0);

UPDATE routes SET 
    average_rating = COALESCE((SELECT AVG(average_rating) FROM reviews WHERE target_id = routes.id AND target_type = 'ROUTE'), 0.0),
    review_count = COALESCE((SELECT COUNT(id) FROM reviews WHERE target_id = routes.id AND target_type = 'ROUTE'), 0);

