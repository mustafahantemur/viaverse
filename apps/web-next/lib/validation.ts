/**
 * Lightweight client-side validation. We deliberately keep this minimal
 * and re-use the server's contract — anything we accept here, the server
 * will re-validate (PasswordPolicy / IdentifierNormalizer / ConsentPolicy)
 * and is the final authority. Goal here is just early UX feedback.
 */

export const PASSWORD_MIN_LENGTH = 10;
export const PASSWORD_MAX_LENGTH = 128;

const HAS_LOWER = /[a-z]/;
const HAS_UPPER = /[A-Z]/;
const HAS_DIGIT = /[0-9]/;
const HAS_SYMBOL = /[^A-Za-z0-9\s]/;

export type PasswordIssue =
    | "tooShort"
    | "tooLong"
    | "missingLower"
    | "missingUpper"
    | "missingDigit"
    | "missingSymbol";

export interface PasswordEvaluation {
    issues: PasswordIssue[];
    score: number; // 0–4, for the strength meter
    isValid: boolean;
}

export function evaluatePassword(password: string): PasswordEvaluation {
    const issues: PasswordIssue[] = [];
    if (password.length < PASSWORD_MIN_LENGTH) issues.push("tooShort");
    if (password.length > PASSWORD_MAX_LENGTH) issues.push("tooLong");
    if (!HAS_LOWER.test(password)) issues.push("missingLower");
    if (!HAS_UPPER.test(password)) issues.push("missingUpper");
    if (!HAS_DIGIT.test(password)) issues.push("missingDigit");
    if (!HAS_SYMBOL.test(password)) issues.push("missingSymbol");

    const classes =
        Number(HAS_LOWER.test(password)) +
        Number(HAS_UPPER.test(password)) +
        Number(HAS_DIGIT.test(password)) +
        Number(HAS_SYMBOL.test(password));
    const lengthScore = Math.min(2, Math.floor(password.length / 8));
    const score = Math.min(4, classes - 1 + lengthScore);

    return { issues, score: Math.max(0, score), isValid: issues.length === 0 };
}

export function describePasswordIssue(issue: PasswordIssue): string {
    switch (issue) {
        case "tooShort":
            return `At least ${PASSWORD_MIN_LENGTH} characters`;
        case "tooLong":
            return `At most ${PASSWORD_MAX_LENGTH} characters`;
        case "missingLower":
            return "A lowercase letter";
        case "missingUpper":
            return "An uppercase letter";
        case "missingDigit":
            return "A digit";
        case "missingSymbol":
            return "A symbol (e.g. !@#$)";
    }
}

/** Permissive identifier check — server is authoritative via libphonenumber. */
export function isLikelyIdentifier(value: string): boolean {
    const trimmed = value.trim();
    if (trimmed.length < 4) return false;
    if (trimmed.includes("@")) {
        // crude email check
        return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(trimmed);
    }
    // phone — at least 8 digits, allow +/space/dash/paren
    const digits = trimmed.replace(/[^0-9]/g, "");
    return digits.length >= 8;
}
