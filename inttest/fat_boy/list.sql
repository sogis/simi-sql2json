SELECT
	'{
	"name": "ch.so.afu.gefahrenhinweiskarte_stfv",
	"type": "layergroup",
	"title": "Störfallverordnung",
	"layers": [{
			"name": "ch.so.afu.gefahrenhinweiskarte_stfv.betriebe",
			"type": "layer",
			"title": "Störfallverordnung - Betriebe",
			"attributes": [
				"geometry"
			],
			"queryable": true
		},
		{
			"name": "ch.so.afu.gefahrenhinweiskarte_stfv.eisenbahn",
			"type": "layer",
			"title": "Störfallverordnung - Eisenbahn",
			"attributes": [
				"geometry"
			],
			"queryable": true
		},
		{
			"name": "ch.so.afu.gefahrenhinweiskarte_stfv.durchgangsstrassen",
			"type": "layer",
			"title": "Störfallverordnung - Durchgangsstrassen ",
			"attributes": [
				"geometry"
			],
			"queryable": true
		}
	],
	"hide_sublayers": true
	}'::jsonb AS json
FROM
	generate_series(1,4000)