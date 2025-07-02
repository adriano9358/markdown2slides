import { Range } from "@codemirror/state"
import { Decoration, DecorationSet, EditorView, ViewPlugin, ViewUpdate, WidgetType } from "@codemirror/view"
import { remoteCursorsField } from "./cursorField"

export const  renderCursorsPlugin = 
  ViewPlugin.fromClass(class {
    decorations: DecorationSet

    constructor(view: EditorView) {
      this.decorations = this.buildDecorations(view)
    }

    update(update: ViewUpdate) {
        if (update.docChanged || update.startState.field(remoteCursorsField) !== update.state.field(remoteCursorsField)) {
            this.decorations = this.buildDecorations(update.view)
        }   
    }

    buildDecorations(view: EditorView): DecorationSet {
      const builder: Range<Decoration>[] = []
      const cursors = view.state.field(remoteCursorsField)
      const docLength = view.state.doc.length;
      for (let [id, { from }] of cursors) {
        if (from > docLength) continue;
        const deco = Decoration.widget({
          widget: new CursorWidget(id),
          side: 1
        })
        builder.push(deco.range(from))
      }
      return Decoration.set(builder)
    }

    destroy() {}

  }, {
    decorations: v => v.decorations
  })


class CursorWidget extends WidgetType {
  constructor(public userId: string) { super() }

  toDOM() {
    const span = document.createElement("span")
    span.className = "remote-cursor"
    span.style.borderLeft = "2px solid red"
    span.style.marginLeft = "-1px"
    span.style.height = "1em"
    span.title = this.userId
    return span
  }
}
