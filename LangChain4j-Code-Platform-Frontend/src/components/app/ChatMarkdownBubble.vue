<script setup lang="ts">
import MarkdownIt from 'markdown-it'
import { computed } from 'vue'

const props = defineProps<{
  content: string
}>()

const markdown = new MarkdownIt({
  breaks: true,
  html: false,
  linkify: true,
  typographer: true,
})

const defaultRender =
  markdown.renderer.rules.link_open ??
  ((tokens, idx, options, _env, self) => self.renderToken(tokens, idx, options))

markdown.renderer.rules.link_open = (tokens, idx, options, env, self) => {
  tokens[idx]?.attrSet('target', '_blank')
  tokens[idx]?.attrSet('rel', 'noopener noreferrer')
  return defaultRender(tokens, idx, options, env, self)
}

const renderedContent = computed(() => markdown.render(props.content || ''))
</script>

<template>
  <div class="chat-markdown" v-html="renderedContent"></div>
</template>

<style scoped>
.chat-markdown {
  color: inherit;
  line-height: 1.75;
  word-break: break-word;
}

.chat-markdown :deep(*:first-child) {
  margin-top: 0;
}

.chat-markdown :deep(*:last-child) {
  margin-bottom: 0;
}

.chat-markdown :deep(p),
.chat-markdown :deep(ul),
.chat-markdown :deep(ol),
.chat-markdown :deep(blockquote),
.chat-markdown :deep(pre),
.chat-markdown :deep(table) {
  margin: 0 0 12px;
}

.chat-markdown :deep(h1),
.chat-markdown :deep(h2),
.chat-markdown :deep(h3),
.chat-markdown :deep(h4) {
  margin: 18px 0 10px;
  color: inherit;
  line-height: 1.35;
}

.chat-markdown :deep(h1) {
  font-size: 1.28rem;
}

.chat-markdown :deep(h2) {
  font-size: 1.16rem;
}

.chat-markdown :deep(h3),
.chat-markdown :deep(h4) {
  font-size: 1.02rem;
}

.chat-markdown :deep(ul),
.chat-markdown :deep(ol) {
  padding-left: 22px;
}

.chat-markdown :deep(li + li) {
  margin-top: 6px;
}

.chat-markdown :deep(blockquote) {
  padding: 10px 14px;
  border-left: 4px solid rgba(27, 86, 143, 0.35);
  border-radius: 14px;
  background: rgba(19, 84, 122, 0.06);
}

.chat-markdown :deep(code) {
  padding: 0.12em 0.4em;
  border-radius: 8px;
  background: rgba(16, 40, 61, 0.08);
  font-family:
    'Cascadia Code', 'JetBrains Mono', 'Fira Code', Consolas, 'Courier New', monospace;
  font-size: 0.92em;
}

.chat-markdown :deep(pre) {
  overflow-x: auto;
  padding: 14px 16px;
  border-radius: 16px;
  background: #0f1f2e;
  color: #ecf4ff;
}

.chat-markdown :deep(pre code) {
  padding: 0;
  background: transparent;
  color: inherit;
}

.chat-markdown :deep(a) {
  color: #1e66c7;
  text-decoration: underline;
  text-underline-offset: 2px;
}

.chat-markdown :deep(table) {
  width: 100%;
  border-collapse: collapse;
  border-radius: 14px;
  overflow: hidden;
}

.chat-markdown :deep(th),
.chat-markdown :deep(td) {
  padding: 10px 12px;
  border: 1px solid rgba(17, 38, 58, 0.1);
  text-align: left;
}

.chat-markdown :deep(th) {
  background: rgba(17, 38, 58, 0.06);
}
</style>
