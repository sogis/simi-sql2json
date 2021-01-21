SELECT 
	'{"a": {"b":"§list_ok§"}}'::jsonb AS jtext
FROM 
	generate_series(1,5) AS s(ident)
WHERE ident = 10
