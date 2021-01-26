SELECT
	now()::varchar AS stamp
FROM
	generate_series(1,5)
