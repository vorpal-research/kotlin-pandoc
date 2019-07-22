package ru.spbstu.pandoc.jackson

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.intellij.lang.annotations.Language
import org.testng.annotations.Test
import ru.spbstu.ktuples.Tuple
import ru.spbstu.ktuples.Tuple2
import ru.spbstu.pandoc.*
import kotlin.test.assertEquals

inline fun <reified T : Any> ObjectMapper.readValue(from: String) = readValue(from, T::class.java)

class ModelTest {
    val mapper = constructObjectMapper()


    inline fun <reified T: Any> typeRef() = object : TypeReference<T>(){}
    inline fun <reified T: Any> ObjectMapper.readValue(@Language("JSON") cx: String) =
            readValue<T>(cx, typeRef<T>())

    @Test
    fun testPairs() {
        val v: Tuple2<Int, String> =
                mapper.readValue("[1, \"Hello\"]")
        assertEquals(Tuple(1, "Hello"), v)

        val vv: Tuple2<Int, Tuple2<Int, Double>> =
                mapper.readValue("[1, [1, 3.14]]")
        assertEquals(Tuple(1, Tuple(1, 3.14)), vv)
    }


    @Test
    fun testInline() {
        val inline: Inline = mapper.readValue(
                """
                    {
                      "t": "Quoted",
                      "c": [{ "t": "SingleQuote" }, [{ "t": "Str", "c": "Hello" }]]
                    }
                    """
        )
        assertEquals(Inline.Quoted(QuoteType.SingleQuote, listOf(Inline.Str("Hello"))), inline)
    }

    @Test
    fun testBlock() {
        val block: Block = mapper.readValue(
                """
                    {
                      "t": "Para",
                      "c": [{ "t": "Str", "c": "Hello" }, { "t": "Space" }, { "t": "Str", "c": "World" }]
                    }
                    """
        )
        assertEquals(Block.Para(
                listOf(
                        Inline.Str("Hello"),
                        Inline.Space,
                        Inline.Str("World")
                )
        ), block)
    }

    @Test
    fun testTriples() {
        val link: Inline = mapper.readValue(
                """
                    {
                    "t": "Span",
                    "c": [["Je;;", [], [["width", "80%"]]], [{ "t": "Str", "c" : "hi"} ]]
                    }
                """
        )
        assertEquals(Inline.Span(Attr("Je;;", listOf(), listOf(Tuple("width", "80%"))), listOf(Inline.Str("hi"))), link)
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

        val pd: Pandoc = mapper.readValue(json)

        println(pd)

    }
}