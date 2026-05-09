// Method to fetch and store experiences in sessionStorage
export async function fetchAndStoreExperiences() {
    try {
        const token = JWT.getToken();
        if (!token) throw new Error("User not authenticated");

        const response = await fetch("/api/experiences/get", {
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (!response.ok) throw new Error("Failed to fetch experiences");

        const experienceSentence = await response.text(); // الحصول على الجملة من السيرفر
        const experiences = experienceSentence.trim().split(/\s+/); // تحويل الجملة إلى لائحة حسب المسافات

        // تخزين الخبرات في الجلسة
        sessionStorage.setItem("userExperiences", JSON.stringify(experiences));
        return experiences;
    } catch (error) {
        console.error("Error fetching experiences:", error);
        return [];
    }
}

// Method to get the experiences from sessionStorage
export function getStoredExperiences() {
    const experiences = sessionStorage.getItem("userExperiences");
    return experiences ? JSON.parse(experiences) : [];
}

// Method to convert the experiences array back to a single sentence
export function convertArrayToSentence(experiencesArray) {
    return experiencesArray.join(' ');
}
