SELECT 
	concat('id', rownum) AS ident,
	'{"§mark§": {"b":"fuu"}}'::json AS jtext
FROM 
	generate_series(1,5) AS s(rownum)
