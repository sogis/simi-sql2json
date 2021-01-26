SELECT 
	concat('id', rownum) AS ident,
	'{"a": {"b":"§set_ok§"}}'::json AS jtext
FROM 
	generate_series(1,5) AS s(rownum)
