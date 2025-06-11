const postInput = document.getElementById("postInput");
const postButton = document.getElementById("postButton");
const postsContainer = document.getElementById("postsContainer");
const imageUpload = document.getElementById("imageUpload");
const fileUpload = document.getElementById("fileUpload");
const searchInput = document.getElementById("searchInput");

let selectedImage = null;
let selectedFile = null;

const token = localStorage.getItem("token"); // يتم تخزين التوكن في المتصفح بعد تسجيل الدخول
const API_BASE_URL = "http://localhost:8080/api"; // غيّر الرابط إذا كان لديك دومين أو سيرفر خارجي

// دالة لاستخراج الهاشتاقات من النص
function extractHashtags(text) {
    return text.match(/#[\w\u0600-\u06FF]+/g) || [];
}

// دالة لتحويل البيانات إلى ملف
function dataURLtoFile(dataurl, filename) {
    let arr = dataurl.split(','), mime = arr[0].match(/:(.*?);/)[1],
        bstr = atob(arr[1]), n = bstr.length, u8arr = new Uint8Array(n);
    while (n--) {
        u8arr[n] = bstr.charCodeAt(n);
    }
    return new File([u8arr], filename, {type: mime});
}

// التعامل مع رفع الصور
imageUpload.addEventListener("change", (event) => {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
            selectedImage = e.target.result;
        };
        reader.readAsDataURL(file);
    }
});

// التعامل مع رفع الملفات
fileUpload.addEventListener("change", (event) => {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
            selectedFile = e.target.result;
        };
        reader.readAsDataURL(file);
    }
});

// عند النقر على زر "Post"
postButton.addEventListener('click', async () => {
    const postContent = postInput.value.trim();
    const hashtags = extractHashtags(postContent);

    if (!postContent && !selectedImage && !selectedFile) {
        alert('يرجى كتابة منشور أو رفع صورة أو ملف قبل النشر!');
        return;
    }

    const formData = new FormData();
    formData.append('content', postContent);
    formData.append('hashtags', hashtags.join(' '));

    if (selectedImage) {
        const imageFile = dataURLtoFile(selectedImage, 'image.png');
        formData.append('image', imageFile);
    }

    if (selectedFile) {
        const file = dataURLtoFile(selectedFile, 'file.pdf');
        formData.append('file', file);
    }

    try {
        const response = await fetch(`${API_BASE_URL}/posts`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            body: formData
        });

        if (!response.ok) throw new Error('فشل في إرسال المنشور');

        alert('تم نشر المنشور بنجاح!');
        postInput.value = ''; // مسح محتوى الحقل
        imageUpload.value = ''; // مسح محتوى الرفع
        fileUpload.value = ''; // مسح محتوى الرفع
        selectedImage = null;
        selectedFile = null;

        fetchPosts(); // تحديث المنشورات بعد نشر المنشور
    } catch (error) {
        console.error(error);
        alert('حدث خطأ أثناء النشر. حاول مرة أخرى.');
    }
});

// جلب المنشورات من الخادم
async function fetchPosts(query = '') {
    try {
        const response = await fetch(`${API_BASE_URL}/posts/search?query=${query}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error('فشل في تحميل المنشورات');
        }

        const posts = await response.json();
        displayFilteredPosts(posts); // عرض المنشورات المسترجعة
    } catch (error) {
        console.error(error);
        postsContainer.innerHTML = "<p>تعذر تحميل المنشورات.</p>"; // رسالة في حال فشل تحميل المنشورات
    }
}

// عرض المنشورات في واجهة المستخدم
function displayFilteredPosts(posts) {
    postsContainer.innerHTML = '';
    if (posts.length === 0) {
        postsContainer.innerHTML = "<p>لا توجد منشورات.</p>";
        return;
    }

    posts.forEach(post => {
        const postDiv = document.createElement("div");
        postDiv.classList.add("post");

        postDiv.innerHTML = `
            <p>${post.content}</p>
            <p><strong>هاشتاقات:</strong> ${post.hashtags.join(' ')}</p>
            ${post.user ? `<p><strong>الطالب:</strong> ${post.user.fullName}</p>` : ''}
            ${post.imageUrl ? `<img src="${post.imageUrl}" alt="Image" class="post-image">` : ''}
            ${post.fileUrl ? `<a href="${post.fileUrl}" download>تحميل الملف</a>` : ''}
        `;

        postsContainer.appendChild(postDiv);
    });
}

// البحث في المنشورات عند إدخال نص في حقل البحث
searchInput.addEventListener('input', (e) => {
    const query = e.target.value.trim();
    fetchPosts(query); // جلب المنشورات بناءً على النص المدخل
});

// جلب المنشورات عند تحميل الصفحة
window.addEventListener('DOMContentLoaded', () => {
    fetchPosts(); // جلب المنشورات عند تحميل الصفحة
});
