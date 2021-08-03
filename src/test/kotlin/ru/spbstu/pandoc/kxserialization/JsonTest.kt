package ru.spbstu.pandoc.kxserialization

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import ru.spbstu.pandoc.Inline
import ru.spbstu.pandoc.MetaValue
import ru.spbstu.pandoc.MetaValue.*
import ru.spbstu.pandoc.Pandoc
import kotlin.test.Test
import kotlin.test.assertEquals

class JsonTest {
    @Test
    fun tst() {
        val md = MetaMap(
            mapOf("x" to MetaMap(mapOf("y" to MetaInlines(listOf(Inline.Space)))))
        )

        println(Json.encodeToString(md as MetaValue))
    }

    @Test
    fun testWhole() {
        val json =
            // language=JSON
            """
{
  "blocks": [
    {
      "t": "Header",
      "c": [
        1,
        [
          "head",
          [],
          []
        ],
        [
          {
            "t": "Str",
            "c": "Head"
          }
        ]
      ]
    },
    {
      "t": "Para",
      "c": [
        {
          "t": "Str",
          "c": "blabla"
        },
        {
          "t": "Space"
        },
        {
          "t": "Cite",
          "c": [
            [
              {
                "citationSuffix": [],
                "citationNoteNum": 0,
                "citationMode": {
                  "t": "NormalCitation"
                },
                "citationPrefix": [
                  {
                    "t": "Str",
                    "c": "see"
                  }
                ],
                "citationId": "ropert",
                "citationHash": 0
              }
            ],
            [
              {
                "t": "Str",
                "c": "[see"
              },
              {
                "t": "Space"
              },
              {
                "t": "Str",
                "c": "@ropert]"
              }
            ]
          ]
        }
      ]
    }
  ],
  "pandoc-api-version": [
    1,
    17,
    4,
    2
  ],
  "meta": {}
}
            """

        val pandoc: Pandoc = Json.decodeFromString(json)
        assertEquals(Json.parseToJsonElement(json), Json.encodeToJsonElement(pandoc))
        assertEquals(pandoc, Json.decodeFromJsonElement(Json.parseToJsonElement(json)))
        assertEquals(Json.parseToJsonElement(json), Json.parseToJsonElement(Json.encodeToString(pandoc)))

    }
}