SELECT 
	concat('id', rownum) AS ident
FROM
	generate_series(1,5) AS s(rownum)