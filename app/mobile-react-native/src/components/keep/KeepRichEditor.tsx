import { useEffect, useRef, useState, type ComponentType, type MutableRefObject, type Ref } from "react";
import { ActivityIndicator, Pressable, Text, TextInput, View } from "react-native";
import * as RichRuntime from "react-native-pell-rich-editor";
import type { RichEditor, RichEditorProps } from "react-native-pell-rich-editor";
import { colors } from "../../theme/colors";
import { API_BASE_URL } from "../../config/env";

export type KeepBodyReader = MutableRefObject<(() => Promise<string>) | null>;
const LocalEditor = RichRuntime.RichEditor as unknown as ComponentType<RichEditorProps & { html: string; ref: Ref<RichEditor> }>;
const createDocument = (RichRuntime as unknown as { createHTML(options: Record<string, unknown>): string }).createHTML;

/**
 * HTML 원문을 리치 텍스트로 편집한다. 초기 DOM 직렬화 결과를 기준으로 비교해 미편집 원문을 그대로 보존한다.
 * 접기·필드 전환·저장은 reader로 최신 DOM을 확인한다. 확인 실패는 원문을 평문화하거나 저장하지 않고 오류로 전달한다.
 */
export function KeepRichEditor({ value, reader, onChange, onPending, onError }: {
  value: string; reader?: KeepBodyReader; onChange: (html: string) => void;
  onPending?: (pending: boolean) => void; onError?: (message: string) => void;
}) {
  const editor = useRef<RichEditor>(null);
  const original = useRef(value);
  const baseline = useRef<string | null>(null);
  const readInFlight = useRef<Promise<string> | null>(null);
  const mounted = useRef(true);
  const [ready, setReady] = useState(false);
  const [linkOpen, setLinkOpen] = useState(false);
  const [linkLabel, setLinkLabel] = useState("");
  const [linkUrl, setLinkUrl] = useState("");
  const callbacks = useRef({ onChange, onPending, onError });
  callbacks.current = { onChange, onPending, onError };
  const [document] = useState(() => {
    const nonce = "keepEditor" + Math.random().toString(36).slice(2);
    const html = createDocument({ useContainer: false, inputListener: true, initialFocus: false,
      defaultParagraphSeparator: "p", styleWithCSS: false, useCharacter: true,
      backgroundColor: colors.input, color: colors.text, contentCSSText: "font-size:16px;line-height:1.6;" });
    const policy = "default-src 'none'; script-src 'nonce-" + nonce + "'; style-src 'unsafe-inline'; img-src data: https: http:; media-src https: http:; base-uri http: https:";
    return html.replace("<head>", '<head><meta http-equiv="Content-Security-Policy" content="' + policy + '"><base href="' + API_BASE_URL.replace(/"/g, "&quot;") + '">')
      .replace(/<script>/g, '<script nonce="' + nonce + '">');
  });

  function changed(html: string) {
    if (!mounted.current || baseline.current === null) return;
    callbacks.current.onChange(html === baseline.current ? original.current : html);
    callbacks.current.onPending?.(false);
  }

  async function read(): Promise<string> {
    if (readInFlight.current) return readInFlight.current;
    if (!editor.current || baseline.current === null) throw new Error("본문 에디터가 아직 준비되지 않았습니다.");
    editor.current.sendAction("content", "setDisable", true);
    const task = (async () => {
      const html = await editor.current!.getContentHtml();
      if (!mounted.current) throw new Error("본문 편집이 종료되었습니다.");
      changed(html);
      return html === baseline.current ? original.current : html;
    })();
    readInFlight.current = task;
    try { return await task; } finally {
      readInFlight.current = null;
      if (mounted.current) editor.current?.sendAction("content", "setDisable", false);
    }
  }

  useEffect(() => {
    if (reader) reader.current = read;
    return () => {
      mounted.current = false;
      if (reader) reader.current = null;
    };
  }, []);

  async function initialized() {
    try {
      baseline.current = await editor.current!.getContentHtml();
      if (!mounted.current) return;
      setReady(true);
      console.info("[KeepRichEditor] initialized");
    } catch (error) {
      console.error("[KeepRichEditor] initialization failed", error);
      callbacks.current.onPending?.(true);
      callbacks.current.onError?.("본문 에디터를 준비하지 못했습니다. 입력을 보존했습니다. 변경을 취소한 뒤 내용을 다시 열어 주세요.");
    }
  }

  function format(action: string) {
    if (!ready) return;
    callbacks.current.onPending?.(true);
    editor.current?.sendAction(action, "result");
  }

  function insertLink() {
    if (!/^https?:\/\/[^\s]+$/i.test(linkUrl.trim())) {
      callbacks.current.onError?.("링크는 http:// 또는 https:// 주소로 입력하세요.");
      return;
    }
    callbacks.current.onPending?.(true);
    editor.current?.insertLink(linkLabel.trim() || linkUrl.trim(), linkUrl.trim());
    setLinkOpen(false); setLinkLabel(""); setLinkUrl("");
  }

  return <View style={{ gap: 8 }}>
    <View style={{ flexDirection: "row", flexWrap: "wrap", gap: 6 }}>
      {[
        [RichRuntime.actions.setBold, "굵게"], [RichRuntime.actions.setItalic, "기울임"],
        [RichRuntime.actions.insertBulletsList, "글머리"], [RichRuntime.actions.insertOrderedList, "번호"],
        [RichRuntime.actions.undo, "되돌리기"], [RichRuntime.actions.redo, "다시 실행"]
      ].map(([action, label]) => <Pressable key={action} accessibilityRole="button" accessibilityLabel={"본문 " + label}
        disabled={!ready} onPress={() => format(action)} style={{ padding: 8, borderWidth: 1, borderColor: colors.border, borderRadius: 6 }}>
        <Text style={{ color: colors.accent }}>{label}</Text>
      </Pressable>)}
      <Pressable accessibilityRole="button" accessibilityLabel="본문 링크" disabled={!ready}
        onPress={() => setLinkOpen(!linkOpen)} style={{ padding: 8 }}><Text style={{ color: colors.accent }}>링크</Text></Pressable>
    </View>
    {linkOpen ? <View style={{ gap: 6 }}>
      <TextInput accessibilityLabel="링크 표시 글" placeholder="표시할 글" value={linkLabel} onChangeText={setLinkLabel} />
      <TextInput accessibilityLabel="링크 주소" placeholder="https://" autoCapitalize="none" keyboardType="url" value={linkUrl} onChangeText={setLinkUrl} />
      <Pressable accessibilityRole="button" accessibilityLabel="본문 링크 넣기" onPress={insertLink}><Text style={{ color: colors.accent }}>링크 넣기</Text></Pressable>
    </View> : null}
    {!ready ? <ActivityIndicator color={colors.accent} /> : null}
    <LocalEditor ref={editor} html={document} initialContentHTML={original.current} initialHeight={260}
      useContainer={false} scrollEnabled nestedScrollEnabled style={{ height: 260, backgroundColor: colors.input }}
      accessibilityLabel="본문 리치 텍스트 편집" defaultParagraphSeparator="p" disabled={!ready}
      onChange={changed} onInput={() => callbacks.current.onPending?.(true)}
      editorInitializedCallback={() => { void initialized(); }}
      onError={(event) => {
        setReady(false);
        console.error("[KeepRichEditor] WebView failed", event.nativeEvent.code);
        callbacks.current.onPending?.(true);
        callbacks.current.onError?.("본문 에디터 오류가 발생했습니다. 입력을 보존했습니다. 변경을 취소한 뒤 내용을 다시 열어 주세요.");
      }} />
  </View>;
}