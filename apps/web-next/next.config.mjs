/** @type {import('next').NextConfig} */
const nextConfig = {
    images: {
        // Hero / ProviderCTA temporarily load slice-of-life imagery from
        // Unsplash. Swap with first-party CDN before production launch.
        remotePatterns: [
            {
                protocol: "https",
                hostname: "images.unsplash.com",
            },
        ],
    },
};

export default nextConfig;
