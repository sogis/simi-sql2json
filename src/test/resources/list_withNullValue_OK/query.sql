SELECT 
	'{"a": {"§null_key§": null}}'::jsonb AS jtext
FROM 
	generate_series(1,5)
