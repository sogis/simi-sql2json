SELECT 
	'{"a": {"b":"§list_ok§"}}'::json AS jtext
FROM
	generate_series(1,2)
UNION ALL
SELECT
	null AS jtext
FROM
	generate_series(1,2)
