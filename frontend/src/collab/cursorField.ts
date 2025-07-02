import { StateField, StateEffect } from "@codemirror/state"

export const setRemoteCursors = StateEffect.define<Map<string, { from: number, to: number }>>()

export const remoteCursorsField = StateField.define<Map<string, { from: number, to: number }>>({
  create() {
    return new Map()
  },
  update(value, tr) {
    for (let effect of tr.effects) {
      if (effect.is(setRemoteCursors)) return effect.value
    }
    return value
  },
})