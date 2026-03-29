// Helper to check if the current config.url matches a pattern like /rooms/delete/:id
export const isPatternMatch = (url, pattern) => {
    // Escapes special characters and replaces :id with a regex matching any segment
    const regex = new RegExp(`^${pattern.replace(/:[^\s/]+/g, '([^/]+)')}$`);
    // Axios config.url might be relative or absolute; ensure you test accordingly
    return regex.test(url);
};

export const isUrlMatch = (requestUrl, pattern) => {
    // If the pattern contains ':id', treat it as a path segment wildcard
    // If it contains '\\?', treat it as a literal query string match
    const regexSource = pattern
        .replace(/:[^\s/]+/g, '([^/]+)') // Replace :id or :term with wildcard
        .replace(/\?/g, '\\?');          // Ensure ? is escaped for regex

    const regex = new RegExp(regexSource);
    return regex.test(requestUrl);
};