SELECT 
	'{"a": {"b":"§elem_ok§"}}'::json AS jtext
FROM 
	generate_series(1,1)
