import Image from "next/image";

interface Props {
    size?: number;
    /** Show the wordmark next to the V. Default true. */
    withWordmark?: boolean;
}

/** The orange/green claymation V plus the wordmark lockup. */
export function BrandMark({ size = 36, withWordmark = true }: Props) {
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
                    width={Math.round(size * 1.08)}
                    height={Math.round(size * 0.61)}
                    priority
                />
            )}
        </span>
    );
}
