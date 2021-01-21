SELECT 
	'{"a": {"b":"§list_ok§"}}'::jsonb AS jtext
FROM 
	generate_series(1,5)
