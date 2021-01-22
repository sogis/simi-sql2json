SELECT 
	concat('id', rownum) AS ident,
	'§set_ok§' AS jtext
FROM 
	generate_series(1,5) AS s(rownum)
