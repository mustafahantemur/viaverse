import Image from "next/image";

interface Props {
    /** Height of the V mark in pixels. The wordmark scales relative to this. */
    size?: number;
    /** Show the wordmark next to the V. Default true. */
    withWordmark?: boolean;
    /**
     * Wordmark width-to-V-size multiplier. The wordmark glyph is roughly
     * twice as wide as it is tall, so even at 1× it looks visually
     * smaller than the V. Default 2.4× gives the wordmark the visual
     * weight users expect from the marketing lockup.
     */
    wordmarkScale?: number;
}

/** The orange/green claymation V plus the wordmark lockup. */
export function BrandMark({ size = 80, withWordmark = true, wordmarkScale = 2.1 }: Props) {
    const wordmarkWidth = Math.round(size * wordmarkScale);
    const wordmarkHeight = Math.round(size);
    return (
        <span style={{ display: "inline-flex", alignItems: "center", gap: 10 }}>
            <Image
                src="/brand/assets/viaverse_icon.png"
                alt="Viaverse"
                width={size}
                height={size}
                priority
            />
            {withWordmark && (
                <Image
                    src="/brand/assets/viaverse_wordmark.png"
                    alt=""
                    width={wordmarkWidth}
                    height={wordmarkHeight}
                    priority
                />
            )}
        </span>
    );
}
