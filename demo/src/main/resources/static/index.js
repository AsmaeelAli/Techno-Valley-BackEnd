const postInput = document.getElementById("postInput");
const postButton = document.getElementById("postButton");
const postsContainer = document.getElementById("postsContainer");
const imageUpload = document.getElementById("imageUpload");
const fileUpload = document.getElementById("fileUpload");
const searchInput = document.getElementById("searchInput");
const userResponse = await fetch('/api');
    const userProfile = await userResponse.json();
let selectedImage = null;
let selectedFile = null;

const token = localStorage.getItem("token"); // يتم تخزين التوكن في المتصفح بعد تسجيل الدخول
const API_BASE_URL = "/api"; // غيّر الرابط إذا كان لديك دومين أو سيرفر خارجي

// دالة لاستخراج الهاشتاقات من النص
function extractHashtags(text) {
    return text.match(/#[\w\u0600-\u06FF]+/g) || [];
}

// دالة لتحويل البيانات إلى ملف
function dataURLtoFile(dataurl, filename) {
    let arr = dataurl.split(','), mime = arr[0].match(/:(.*?);/)[1],
        bstr = atob(arr[1]), n = bstr.length, u8arr = new Uint8Array(n);
    while(n--){
        u8arr[n] = bstr.charCodeAt(n);
    }
    return new File([u8arr], filename, {type:mime});
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


async function fetchPosts(query = '') {
    try {
        const response = await fetch(`${API_BASE_URL}/posts/search?query=${query}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error('Error fetching posts');
        }

        const posts = await response.json();
        displayFilteredPosts(posts);  // Handle the posts in the frontend
    } catch (error) {
        console.error(error);
        alert('Failed to fetch posts.');
    }
}

async function createPost(postContent, hashtags) {
    const formData = new FormData();
    formData.append('content', postContent);
    formData.append('hashtags', hashtags.join(' '));

    try {
        const response = await fetch(`${API_BASE_URL}/posts`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
            },
            body: formData
        });

        if (!response.ok) {
            throw new Error('Failed to create post');
        }

        alert('Post created successfully!');
        fetchPosts();  // Refresh the posts after creating a new one
    } catch (error) {
        console.error(error);
        alert('Error creating post');
    }
}
document.getElementById('profile-name').textContent = `name: ${userProfile.name}`;
    document.getElementById('profile-id').textContent = userProfile.id;
    document.getElementById('profile-role').textContent = userProfile.role;
    document.getElementById('profile-email').textContent = userProfile.email;
    document.getElementById('profile-university').textContent = userProfile.university;
    
    // الأزرار to post//////////////
    document.getElementById('post-btn').addEventListener('click', async () => {

        const response = await fetch('/api');
    
        const posts = await response.json();
        const contentCard = document.getElementById('content-card');
        contentCard.innerHTML = `
            <h3>المنشورات</h3>
            <ul>
                ${posts.map(post => `<li>${post.content} - <strong>${post.isLiked ? 'Liked' : 'Not Liked'}</strong> - <strong>${post.isSaved ? 'Saved' : 'Not Saved'}</strong></li>`).join('')}
            </ul>
        `;
    });
/////////////to like
    document.getElementById('like-btn').addEventListener('click', async () => {

        const response = await fetch('/api', { method: 'POST' });
        if (response.ok) {
            const contentCard = document.getElementById('content-card');
            contentCard.innerHTML = '<p>تم الإعجاب بالمنشور.</p>';
        }
    });
///////////// to seved
    document.getElementById('saved-btn').addEventListener('click', async () => {

        const response = await fetch('/api', { method: 'POST' });
        if (response.ok) {
            const contentCard = document.getElementById('content-card');
            contentCard.innerHTML = '<p>تم حفظ المنشور.</p>';
        }
    });
});
////////////////////////////////// كارد الخبرات///////////////////////
document.getElementById("experience-form").addEventListener("submit", async function (e) {
    e.preventDefault();
    const input = document.getElementById("experience-input");
    const experienceText = input.value.trim();

    if (experienceText === "") return;

    const response = await fetch("/api", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            userId: "120212211005", // يمكن تغييره بناءً على المستخدم الحالي عدلها يا اسماعيل انو حسب اليوزر الحالي
            experience: experienceText
        })
    });

    if (response.ok) {
        const li = document.createElement("li");
        li.textContent = experienceText;
        document.getElementById("experience-list").appendChild(li);
        input.value = "";
    } else {
        alert("فشل في حفظ الخبرة");
    }
});
//////////////// عرض الخبرات ////////////
const userId = "120212211005"; // يمكن ربطها من الباك اند عند تسجيل الدخول

async function loadExperiences() {
    try {
        const res = await fetch(`/api${userId}`);
        if (!res.ok) throw new Error("Error");
        const experiences = await res.json();

        const list = document.getElementById("experience-list");
        list.innerHTML = ""; // مسح القديم

        experiences.forEach(exp => {
            const li = document.createElement("li");
            li.textContent = exp;
            list.appendChild(li);
        });
    } catch (err) {
        console.error(err);
    }
}
//create post
function handleFileSelect(event) {
    const files = event.target.files;
    const fileInfo = document.getElementById('fileInfo');
    
    if (files.length > 0) {
        const fileNames = Array.from(files).map(file => file.name).join(', ');
        fileInfo.textContent = `Selected files: ${fileNames}`;
    } else {
        fileInfo.textContent = '';
    }
}

function handleSubmit(event) {
    event.preventDefault();
    const form = document.getElementById('createPostForm');
    const postText = form.postContent.value;
    const files = document.getElementById('fileInput').files;
    const errorMessage = document.getElementById('errorMessage');
    
    if (postText.trim() === '' && files.length === 0) {
        errorMessage.style.display = 'block';
        errorMessage.textContent = 'Please enter some text or select files to post';
        return false;
    }

    // Here you would typically send the data to your server
    // For demonstration, we'll create a FormData object
    const formData = new FormData(form);
    
    // Log the form data (for demonstration)
    console.log('Post text:', postText);
    console.log('Files:', files);
    
    // Clear the form
    form.reset();
    document.getElementById('fileInfo').textContent = '';
    errorMessage.style.display = 'none';
    
    alert('Post created successfully!');
    return false;
}

// عند تحميل الصفحة
window.addEventListener("DOMContentLoaded", () => {
    loadExperiences();
});