SELECT 
	'{"§mark§": {"b":"fuu"}}'::jsonb AS jtext
FROM 
	generate_series(1,5)
