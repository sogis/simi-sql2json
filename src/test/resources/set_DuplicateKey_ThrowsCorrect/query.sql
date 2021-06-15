SELECT 
	'duplicate_key' AS ident,
	'{"a": {"b":"§set_ok§"}}'::json AS jtext
FROM 
	generate_series(1,2) AS s(rownum)
